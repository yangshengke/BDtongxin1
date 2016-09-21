package lenovo.bdtongxin.SMS;

import android.app.Activity;
import android.content.Intent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BaseIntentUtil {

    public static int DEFAULT_ENTER_ANIM;
    public static int DEFAULT_EXIT_ANIM;

    private static Intent intent;

    /**
     * �Զ��嶯�� ʹ��
     * DEFAULT_ENTER_ANIM �� DEFAULT_EXIT_ANIM ��Ϊ����Ч��
     * @param activity
     * @param classes
     */
    public static void intentDIY(Activity activity, Class<?> classes){
        intentDIY(activity, classes, null, DEFAULT_ENTER_ANIM, DEFAULT_EXIT_ANIM);
    }

    /**
     * �Զ��嶯�� ʹ��
     * DEFAULT_ENTER_ANIM �� DEFAULT_EXIT_ANIM ��Ϊ����Ч��
     * @param activity
     * @param classes
     * @param paramMap �������
     */
    public static void intentDIY(Activity activity, Class<?> classes, Map<String, String> paramMap){
        intentDIY(activity, classes, paramMap, DEFAULT_ENTER_ANIM, DEFAULT_EXIT_ANIM);
    }

    /**
     * �Զ��嶯��
     * @param activity
     * @param classes
     * @param enterAnim enter��ԴID
     * @param exitAnim  exit��ԴID
     */
    public static void intentDIY(Activity activity, Class<?> classes, int enterAnim, int exitAnim){
        intentDIY(activity, classes, null, enterAnim ,exitAnim);
    }

    /**
     * �Զ��嶯��
     * @param activity
     * @param classes
     * @param paramMap  �������
     * @param enterAnim enter��ԴID
     * @param exitAnim  exit��ԴID
     */
    public static void intentDIY(Activity activity, Class<?> classes, Map<String, String> paramMap, int enterAnim, int exitAnim){
        intent = new Intent(activity, classes);
        organizeAndStart(activity, classes, paramMap);
        if(enterAnim != 0 && exitAnim != 0){
            activity.overridePendingTransition(enterAnim, exitAnim);
        }
    }

    /**
     * ϵͳĬ��
     * @param activity　从这个activity启动
     * @param classes　被启动的activity
     * @param paramMap
     */
    public static void intentSysDefault(Activity activity, Class<?> classes, Map<String, String> paramMap){
        organizeAndStart(activity, classes, paramMap);
    }

    private static void organizeAndStart(Activity activity, Class<?> classes, Map<String, String> paramMap){
        intent = new Intent(activity, classes);
        if(null != paramMap){
            Set<String> set = paramMap.keySet();
            for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {//把每一个key及对应的value放入intent中
                String key = iterator.next();
                intent.putExtra(key, paramMap.get(key));
            }
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//表示启动的Activity会将Task中位于其上的Activity都强制出栈，使其自身位于栈顶
        activity.startActivity(intent);
    }

}
