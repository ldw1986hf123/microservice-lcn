package banka;


import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description TODO
 * @Author shen
 * @Date 2019/2/26 17:52
 **/
public class Service {

    @Autowired
    AccountDao accountDao;

    @Autowired
    BankBClient bankBClient;

    @LcnTransaction
    public String start(int money) {
        String user = "shen";
        String state = bankBClient.addMoney(money,user);

//        int t=1/0;
//        System.out.print(t);
        if ("success".equals(state)){
            Account account =  new Account();
            account.setMoney(money);
            account.setUser("bb");
            int res = accountDao.insert(account);
        }
        return "error";
    }


    public String singAdd (int money) {
        Account account =  new Account();
        account.setMoney(money);
        account.setUser("single'");
        int res = accountDao.insert(account);
        return  "success";
    }
}
