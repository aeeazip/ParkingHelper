package ddwucom.mobile.ma02_20201019;

import android.text.Html;
import android.text.Spanned;

/* 네이버 블로그 api 검색 결과 담는 객체 */
public class NaverBlogDto {
    private int _id;
    private String blogTitle;
    private String description;
    private String bloggername;
    private String link;
    private String postdate;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getBlogTitle() {
        Spanned spanned = Html.fromHtml(blogTitle);     // 문자열에 HTML 태그가 포함되어 있을 경우 제거 후 일반 문자열로 변환
        return spanned.toString();
    }

    public void setBlogTitle(String title) {
        this.blogTitle = title;
    }

    public String getDescription() {
        Spanned spanned = Html.fromHtml(description);     // 문자열에 HTML 태그가 포함되어 있을 경우 제거 후 일반 문자열로 변환
        return spanned.toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBloggername() {
        return bloggername;
    }

    public void setBloggername(String bloggername) {
        this.bloggername = bloggername;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    @Override
    public String toString() {
        return "NaverBlogDto{" +
                "_id=" + _id +
                ", title='" + blogTitle + '\'' +
                ", description='" + description + '\'' +
                ", bloggername='" + bloggername + '\'' +
                ", bloggerlink='" + link + '\'' +
                ", postdate='" + postdate + '\'' + '}' + "\n";
    }
}
