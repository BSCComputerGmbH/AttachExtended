package com.gluonhq.helloandroid.nfc;

import java.util.ArrayList;
import java.util.List;

public class RequestResponseDivier {
	
	
	public static List<GenericPairVO<? extends ARequest, ? extends AResponse>> getGenericPairList(String completeMessage)
	{
		List<GenericPairVO<? extends ARequest, ? extends AResponse>> genericPairList = new ArrayList<GenericPairVO<? extends ARequest, ? extends AResponse>> ();
		//jetzt die Testzerlegung
		
		//1. entfernen des SequenceReqeustCall
		int startIndex = completeMessage.indexOf(ContentTags.SequenceRequestCall.getStartTag());
		int endIndex = completeMessage.indexOf(ContentTags.SequenceRequestCall.getEndTag());
		String contentOfTag = completeMessage.substring(ContentTags.SequenceRequestCall.getStartTag().length(), endIndex);
		System.out.println("contentOfTag " + contentOfTag);
		
		//2. alle Sequenzen in einer einer Liste packen GenericPair<Request, Response>
		int sequenceIndex = 0;
		boolean hasMore = true;
		do
		{
			startIndex = contentOfTag.indexOf(ContentTags.getSequenceStartTag(sequenceIndex));
			endIndex = contentOfTag.indexOf(ContentTags.getSequenceEndTag(sequenceIndex));
			if(startIndex == -1 || endIndex == -1)
			{
				hasMore = false;
				break;
			}
			
			//request/response content
			String contentOfSequence = contentOfTag.substring(startIndex + ContentTags.getSequenceStartTag(sequenceIndex).length(), endIndex);
			
			//substring contentOfSequence in request and response
			int startIndexReq = contentOfSequence.indexOf(ContentTags.Request.getStartTag());
			int endIndexReq = contentOfSequence.indexOf(ContentTags.Request.getEndTag());
			
			int startIndexResp = contentOfSequence.indexOf(ContentTags.Response.getStartTag());
			int endIndexResp = contentOfSequence.indexOf(ContentTags.Response.getEndTag());
			
			String contentOfRequest = contentOfSequence.substring(startIndexReq + ContentTags.Request.getStartTag().length(), endIndexReq);
			System.out.println("contentOfRequest " + contentOfRequest);
			
			String contentOfResponse = contentOfSequence.substring(startIndexResp + ContentTags.Response.getStartTag().length(), endIndexResp);
			System.out.println("contentOfResponse " + contentOfResponse);
			
			genericPairList.add(createGenericPair(contentOfRequest, contentOfResponse));
			
			sequenceIndex++;
		}
		while(hasMore);
		
		
		
		
		
		
		return genericPairList;
		
	}

	private static GenericPairVO<? extends ARequest, ? extends AResponse> createGenericPair(String contentOfRequest, String contentOfResponse) 
	{
		if(contentOfRequest.startsWith("0x"))
		{
			return new GenericPairVO<ByteArrayRequest, ByteArrayResponse>(new ByteArrayRequest(contentOfRequest), new ByteArrayResponse(contentOfResponse));
		}
		return null;
	}
}
