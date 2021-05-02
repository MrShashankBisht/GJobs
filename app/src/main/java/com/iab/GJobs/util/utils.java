package com.iab.GJobs.util;

public class utils {
    private static String removerLineAtPosition(String fromRemove, int position, TAG lastTag){
        String resultString = "";
        int i=0;
        int tagPosition = -1;
        // getting the lastTag position and then remove all string bellow that include that line too.
        // this for loop is for getting the position of the tag
        for(String str: fromRemove.split("\\n")){
            if(lastTag == TAG.TD){
                if(str.contains("<td")){
                    tagPosition = i;
                }
            }else if(lastTag == TAG.TR){
                if(str.contains("<tr")){
                    tagPosition = i;
                }
            }else{

            }
            i++;
        }
        // againg set i to 0
        i = 0;
        for(String str: fromRemove.split("\\n")){
            if(i < tagPosition){
                resultString += (str + "\n");
            }
            i++;
        }
        return resultString;
    }

    public static String getFinalString(String htmlString){
        int i = 0;
        TAG lastTag = TAG.TABLE;
        String finalString = "";
        boolean isRemoveBellowStringActive = false;
        for(String str : htmlString.split("\\n")){
            if(str.contains("<tr")){
                lastTag = TAG.TR;
            }else if(str.contains("<td")){
                lastTag = TAG.TD;
            }else{

            }

            if(str.contains("sarkariresult") || str.contains("Sarkari Result") || str.contains("SARKARI RESULT") || str.contains("Android Apps")
                    || str.contains("Apple IOS Apps") || str.contains("Android") || str.contains("IOS") || str.contains("Interested Candidates Can Read")
                    || str.contains("SarkariResult") || str.contains("Download Mobile Apps")
            || str.contains("How to Fill Form (Video Hindi)") || str.contains("Download Notification") || str.contains("www.youtube.com")
            || str.contains("Download Go")|| str.contains("Widow Apps")|| str.contains("Window Apps") || str.contains("Video Hindi")){
                // remove that block
                finalString = removerLineAtPosition(finalString, i, lastTag);
                isRemoveBellowStringActive = true;
            }else{

            }
            if(!isRemoveBellowStringActive){
                finalString += (str + "\n");
            }
            if(isRemoveBellowStringActive){
                if(lastTag == TAG.TR){
                    if(str.contains("</tr")){
                        isRemoveBellowStringActive = false;
                    }
                }else if(lastTag == TAG.TD){
                    if(str.contains("</td")){
                        isRemoveBellowStringActive = false;
                    }
                }
            }
            i++;
        }
        return finalString;
    }

    enum TAG { TABLE, TD, TR}

}
