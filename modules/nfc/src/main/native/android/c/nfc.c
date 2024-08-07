/*
 * Copyright (c) 2021, Gluon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL GLUON BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
#include "util.h"

static jclass jGraalNFCScanClass;
static jmethodID jGraalResultNFCScanMethod;

static jclass jNfcServiceClass;
static jobject jDalvikNfcService;
static jmethodID jNfcServiceCallMethod;


static void initializeGraalHandles(JNIEnv* env) {
     ATTACH_LOG_FINE("initializeGraalHandles first");
     jGraalNFCScanClass = (*env)->NewGlobalRef(env, (*env)->FindClass(env, "com/gluonhq/attachextended/nfc/impl/AndroidNfcService"));
     ATTACH_LOG_FINE("initializeGraalHandles second");
     jGraalResultNFCScanMethod = (*env)->GetStaticMethodID(env, jGraalNFCScanClass, "setResult", "(Ljava/lang/String;)V");
     ATTACH_LOG_FINE("initializeGraalHandles third");
}


static void initializeDalvikHandles() {
    jNfcServiceClass = GET_REGISTER_DALVIK_CLASS(jNfcServiceClass, "com/gluonhq/helloandroid/DalvikNfcService");
    ATTACH_DALVIK();
    jmethodID jNfcServiceInitMethod = (*dalvikEnv)->GetMethodID(dalvikEnv, jNfcServiceClass, "<init>", "(Landroid/app/Activity;)V");
    jNfcServiceCallMethod = (*dalvikEnv)->GetMethodID(dalvikEnv, jNfcServiceClass, "doConnectWithNFC", "(Ljava/lang/String;)V");

    jobject jActivity = substrateGetActivity();
    jobject jtmpobj = (*dalvikEnv)->NewObject(dalvikEnv, jNfcServiceClass, jNfcServiceInitMethod, jActivity);
    jDalvikNfcService = (*dalvikEnv)->NewGlobalRef(dalvikEnv, jtmpobj);
    DETACH_DALVIK();
}

//////////////////////////
// From Graal to native //
//////////////////////////


JNIEXPORT jint JNICALL
JNI_OnLoad_nfc(JavaVM *vm, void *reserved)
{
    ATTACH_LOG_INFO("JNI_OnLoad_nfc called");
#ifdef JNI_VERSION_1_8
    JNIEnv* graalEnv;
    if ((*vm)->GetEnv(vm, (void **)&graalEnv, JNI_VERSION_1_8) != JNI_OK) {
        ATTACH_LOG_WARNING("Error initializing native Nfc from OnLoad");
        return JNI_FALSE;
    }
    ATTACH_LOG_FINE("[Nfc Service] Initializing native Nfc from OnLoad");
    initializeGraalHandles(graalEnv);
    initializeDalvikHandles();
    return JNI_VERSION_1_8;
#else
    #error Error: Java 8+ SDK is required to compile Attach
#endif
}

// from Java to Android

JNIEXPORT void JNICALL Java_com_gluonhq_attachextended_nfc_impl_AndroidNfcService_sendMessage
(JNIEnv *env, jclass jClass, jstring jmessage)
{
    const char *messageChars = (*env)->GetStringUTFChars(env, jmessage, NULL);
    ATTACH_DALVIK();
    jstring dmessage = (*dalvikEnv)->NewStringUTF(dalvikEnv, messageChars);
    (*dalvikEnv)->CallVoidMethod(dalvikEnv, jDalvikNfcService, jNfcServiceCallMethod, dmessage);
    DETACH_DALVIK();
    (*env)->ReleaseStringUTFChars(env, jmessage, messageChars);
}

//from Android to Application
JNIEXPORT void JNICALL Java_com_gluonhq_helloandroid_DalvikNfcService_nativeSetMessageToApplication(
    JNIEnv *env, jobject service, jstring resultFromNFCSensor) {
    const char *resultChars = (*env)->GetStringUTFChars(env, resultFromNFCSensor, NULL);
    if (isDebugAttach()) {
        ATTACH_LOG_FINE("NFCScanResult result %s\n", resultChars);
    }
    ATTACH_GRAAL();
    jstring jresult = (*graalEnv)->NewStringUTF(graalEnv, resultChars);
    (*graalEnv)->CallStaticVoidMethod(graalEnv, jGraalNFCScanClass, jGraalResultNFCScanMethod, jresult);
    DETACH_GRAAL();
    // (*env)->ReleaseStringUTFChars(env, result, resultChars);
}


