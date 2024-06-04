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
package com.gluonhq.helloandroid;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class DalvikNfcService {

    private static final String TAG = Util.TAG;

    private final Activity activity;
    private final boolean debug;
    
    private Intent intent;
    
    public DalvikNfcService(Activity activity) {
        this.activity = activity;
        this.debug = Util.isDebug();
        
        intent = new Intent(NFC.ACTION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        
        
    }
    
    public void doConnectWithNFC(String optionalDataToSend)
    {
    // Gluon eigenes Ding aus dem Util Paket
        IntentHandler intentHandler = new IntentHandler(){

            @Override
            public void gotActivityResult(int requestCode, int resultCode, Intent intent) {
                System.out.println("DalvikNFCService#gotActivityResult " + intent.toString());
                //TODO nur dann wenn es wirklich auf startActivitForResult hinausläuft.
                if (resultCode == Activity.RESULT_OK) {
                    //TODO key String
                    String result = (String) intent.getExtras().get("SCAN_RESULT");


                    //TODO
                    nativeSetMessageToApplication(result);
                }


            }
        };
        Util.setOnActivityResultHandler(intentHandler);
       
        this.activity.startActivityForResult(intent, Activity.RESULT_OK);

    }


    private void nativeSetMessageToApplication(String resultFromNFCSensor)
    {
        //TODO hier wieder die spätere brücke ansprechen
        //im moment die fakeLib
        NativeFakeLibrary.setMessageResult(resultFromNFCSensor);

    }

    /* TODO raus
    private void log(String message) {
        if (message == null || message.isEmpty()) {
            Log.e(TAG, "Invalid message: message was null or empty");
            return;
        }
        if (debug) {
            Log.d(TAG, "Logging message: " + message);
        }
        Log.v(TAG, message);
    }*/
}
