package Core.Data;

import gr.uoc.csd.hy463.NXMLFileReader;

public class Tags {

    private enum tagList {
        PMC_ID,
        Title,
        Abstract,
        Body,
        Journal,
        Publisher,
        Authors,
        Categories
    }

    public static int tag2num(String tag) {
        tag = tag.replace(" ", "_");
        tagList currTag = tagList.valueOf(tag);
        switch (currTag) {
            case PMC_ID:
                return 0;
            case Title:
                return 1;
            case Abstract:
                return 2;
            case Body:
                return 3;
            case Journal:
                return 4;
            case Publisher:
                return 5;
            case Authors:
                return 6;
            case Categories:
                return 7;
            default:
                assert (false);
        }
        return -1;
    }

    public static String num2tag(final int num) {
        switch (num) {
            case 0:
                return "PMC ID";
            case 1:
                return "Title";
            case 2:
                return "Abstract";
            case 3:
                return "Body";
            case 4:
                return "Journal";
            case 5:
                return "Publisher";
            case 6:
                return "Authors";
            case 7:
                return "Categories";
            default:
                assert (false);
        }
        return null;
    }

    public static String getText(final NXMLFileReader file, String tag) {
        tag = tag.replace(" ", "_");
        tagList currTag = tagList.valueOf(tag);
        switch (currTag) {
            case PMC_ID:
                return file.getPMCID();
            case Title:
                return file.getTitle();
            case Abstract:
                return file.getAbstr();
            case Body:
                return file.getBody();
            case Journal:
                return file.getJournal();
            case Publisher:
                return file.getPublisher();
            default:
                assert (false);
        }
        return null;
    }
}
