package po.lemon.test2.Util;

import java.util.Random;

/**
 * @author mosaic
 * @date 2021/7/25-1:29
 */
public class PhoneRandomUtil {

    public static void main(String[] args) {

        //思路一、先查询手机号码字段，按照倒叙排列，取得最大的手机号码+1

        //思路二、先去生成一个随机的手机号码，再通过该号码进入到数据库查询，如果查询有记录，再来生成一个，否则说明该号码没有被注册（循环）

        System.out.println(getUnregisterPhone());
    }

    public static String getRandomPhone() {

        String phonePrefix = "133";

        //nextInt（a,b）随机生成一个整数，范围是从[a-b)之间
        //生成8位随机整数 - 循环拼接
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            //生成一个[0-8]的随机整数
            int num = random.nextInt(9);
            phonePrefix = phonePrefix + num;
        }
        return phonePrefix;
    }

    public static String getUnregisterPhone() {

        String Phone = "";
        while (true) {

            Phone = getRandomPhone();

            Object result = JUMT.querySingleData("select count(*) from member where mobile_phone =" + Phone);

            if ((Long) result == 0) {
                //表示没有被注册，符合需求
                break;
            } else {
                //需要重新生成
//                Phone = null;
                continue;
            }
        }
        return Phone;
    }
}

