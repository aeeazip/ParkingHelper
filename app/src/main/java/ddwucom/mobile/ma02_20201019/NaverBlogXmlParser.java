package ddwucom.mobile.ma02_20201019;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/* 네이버 블로그 api paring */
public class NaverBlogXmlParser {
    private String TAG = "NaverBlogXmlParser";

    private enum TagType { NONE, TITLE, DESCRIPTION, BLOGGERNAME, LINK, POSTDATE };

    private final static String FAULT_RESULT = "faultResult";
    private final static String ITEM = "item";
    private final static String TITLE = "title";
    private final static String DESCRIPTION = "description";
    private final static String BLOGGERNAME = "bloggername";
    private final static String LINK = "link";
    private final static String POSTDATE = "postdate";

    private XmlPullParser parser;

    public NaverBlogXmlParser() {
        try {
            parser = XmlPullParserFactory.newInstance().newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<NaverBlogDto> parse(String xml) {
        ArrayList<NaverBlogDto> resultList = new ArrayList<NaverBlogDto>();
        NaverBlogDto dto = null;
        TagType tagType = TagType.NONE;

        try{
            parser.setInput(new StringReader (xml));
            int eventType = parser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){
                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();

                        if(tag.equals(ITEM))
                            dto = new NaverBlogDto();
                        else if(tag.equals(TITLE)) {
                            if(dto != null)
                                tagType = TagType.TITLE;
                        }
                        else if(tag.equals(DESCRIPTION)) {
                            if(dto != null)
                                tagType = TagType.DESCRIPTION;
                        }
                        else if(tag.equals(BLOGGERNAME))
                            tagType = TagType.BLOGGERNAME;
                        else if(tag.equals(LINK)){
                            if(dto != null)
                                tagType = TagType.LINK;
                        }
                        else if(tag.equals(POSTDATE))
                            tagType = TagType.POSTDATE;
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals(ITEM))
                            resultList.add(dto);
                        break;
                    case XmlPullParser.TEXT:
                        switch(tagType){
                            case TITLE:
                                dto.setBlogTitle (parser.getText());
                                break;
                            case DESCRIPTION:
                                if(parser.getText() == null)
                                    dto.setDescription("No data");
                                else
                                    dto.setDescription (parser.getText());
                                break;
                            case BLOGGERNAME:
                                if(parser.getText()!=null)
                                    dto.setBloggername(parser.getText());
                                else
                                    dto.setBloggername("No data");
                                break;
                            case LINK:
                                if(parser.getText()!=null)
                                    dto.setLink(parser.getText());
                                else
                                    dto.setLink("No data");
                                break;
                            case POSTDATE:
                                if(parser.getText()!=null)
                                    dto.setPostdate(parser.getText());
                                else
                                    dto.setPostdate("No data");
                                break;
                        }
                        tagType = TagType.NONE;
                        break;
                }
                eventType = parser.next();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return resultList;
    }
}
