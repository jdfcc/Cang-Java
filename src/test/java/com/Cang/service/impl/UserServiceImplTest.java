package com.Cang.service.impl;

import com.Cang.entity.Count;
import com.Cang.entity.User;
import com.Cang.mapper.CountMapper;
import com.Cang.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.LinkedList;


/**
 * @author Jdfcc
 * @Description TODO
 * @DateTime 2023/6/5 19:19
 */

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceImplTest {
    @Autowired
    private UserMapper mapper;

    @Autowired
    private CountMapper countMapper;

    @Test
    void  testCount(){
        Count count = countMapper.selectVoucher();
        System.out.println(count);
    }

    @Test
    void testLength(){
        int count = 9;
        System.out.println(count/2);
        System.out.println(count%2);
    }

    @Test
    void testGet(){
        int x=1234321;
        String xString = String.valueOf(x);
        int len = xString.length() / 2;
        ArrayList<String> arr = new ArrayList<String>();

        for (int i = 0; i < xString.length(); i++) {
            arr.add(String.valueOf(xString.charAt(i)));
        }

        String front;
        String back;

        boolean flag=true;

        for (int i = 0; i <len; i++) {
            front=arr.get(i);
            back=arr.get(xString.length()-i-1);
            if (!front.equals(back)){
                flag=false;
            }
        }

        System.out.println(flag) ;
    }

    @Test
    public void testStep(){
        for (int i=1; i<1;i++){
            System.out.println("Step " + i);
        }
    }
    @Test
    void getAvatar() {
        LambdaQueryWrapper<User> wrapper = Wrappers.lambdaQuery(User.class);
        wrapper.eq(User::getId, 1010L);
        User user = mapper.selectOne(wrapper);
        String icon = user.getIcon();
        log.info("icon {}", icon);
    }

    static class Student {
        public String name;

        public Student(String name) {
            this.name = name;
        }

        @Override
        public String toString() {

            return this.name;
        }
    }

    @Test
    void testArr() {
        Student lisi = new Student("lisi");
        Student[] students = new Student[5];
        students[0] = lisi;

        System.out.println(students[0]);
    }

    @Test
    void testList(){
        LinkedList<Integer> integers = new LinkedList<>();
        integers.add(5,5);
        ArrayList<Integer> objects = new ArrayList<>();
        objects.add(4,3);

    }

}