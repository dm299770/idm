import org.junit.Test;

import java.io.UnsupportedEncodingException;

public class Ut8Test {
    @Test
    public  void test(){
        String msg = "【尼桑中国】验证码为102400";
        try {
            byte[] bytes = msg.getBytes("utf-8");
            String utf8 = new String(bytes);
            System.out.println(utf8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }


}
