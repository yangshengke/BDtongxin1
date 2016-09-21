package lenovo.bdtongxin.SMS;

/**
 * Created by lenovo on 2016/9/7.
 */
public class MessageBean {
    private String name;
    private String date;//短信日期
    private String text;//短信内容
    private int layoutID;

    public MessageBean(){
    }

    public MessageBean(String name, String date, String text, int layoutID){
        super();
        this.name = name;
        this.date = date;
        this.text = text;
        this.layoutID = layoutID;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    public int getLayoutID(){
        return layoutID;
    }

    public void setLayoutID(int layoutID){
        this.layoutID = layoutID;
    }
}
