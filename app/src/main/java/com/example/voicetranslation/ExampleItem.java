package com.example.voicetranslation;

public class ExampleItem {

    private int mImageResource;
    private String mtextView_from;
    private String mtextView_to;

    public ExampleItem(int ImageResource,String textView_from,String textView_to)
    {
        mImageResource = ImageResource;
        mtextView_from=textView_from;
        mtextView_to = textView_to;

    }

    public int getmImageResource()
    {
        return mImageResource;
    }
    public String getMtextView_from()
    {
        return mtextView_from;
    }
    public String getMtextView_to()
    {
        return mtextView_to;
    }

}
