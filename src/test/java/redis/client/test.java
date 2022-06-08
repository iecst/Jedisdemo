package redis.client;

import org.junit.jupiter.api.Test;



public class test {


    @Test
    void T1(){
        Jedis jedis = new Jedis("192.168.1.11", 6379);
        System.out.println();
        String setValue = jedis.set("startKey", "startValue");
        String s = "\\d+\"";

      String ss=  "//div[@class='BlogEntity']/div[@class='BlogTitle']/h1";
        System.out.println();
    }


}
