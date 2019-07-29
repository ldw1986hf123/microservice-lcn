package banka;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AccountDao {

    @Update("update bank_a set money = money-#{money} where user=#{user}")
    int update(Account account);


    @Update("insert into  bank_a (money,user) values(#{money},#{user})")
    int insert(Account account);
}
