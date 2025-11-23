package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags = "C端地址簿接口")
@Slf4j
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 查询当前登录用户的所有地址信息
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("查询当前登录用户的所有地址信息")
    public Result<List<AddressBook>> list(){
        AddressBook addressBook = new AddressBook();
        addressBook.setUserId(BaseContext.getCurrentId());
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    /**
     * 新增地址
     * @param addressBook
     * @return
     */
    @PostMapping
    @ApiOperation("新增地址")
    public Result save(@RequestBody AddressBook addressBook){
        //添加测试日志
        log.error("add方法接收到的sex值为：{}",addressBook.getSex());

        addressBookService.save(addressBook);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    /**
     * 根据id修改地址
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("根据id修改地址")
    public Result update(@RequestBody AddressBook addressBook){
        //添加测试日志
        log.error("update方法接收到的sex值为：{}",addressBook.getSex());
        //调用接口的更新方法
        addressBookService.update(addressBook);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefault(@RequestBody AddressBook addressBook){
        //从接口获取默认状态
        addressBookService.setDefault(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteById(Long id){   //这个传递进来的参数id从哪获取呢？
        addressBookService.deleteById(id);
        return Result.success();
    }

    /**
     * 查询默认地址
     * @return
     */
    @GetMapping("default")
    @ApiOperation("查询地址")
    public Result<AddressBook> getDefault(){
        AddressBook addressBook = new AddressBook();
        //设置默认id
        addressBook.setIsDefault(1);
        //获取当前用户登录id
        addressBook.setUserId(BaseContext.getCurrentId());
        //将添加地址对象赋值给list集合
        List<AddressBook> list = addressBookService.list(addressBook);
        //判断每个新增地址是否存在，并赋予非默认地址
        if( list !=null && list.size() == 1){ //这一段代码逻辑是什么？
            return Result.success(list.get(0)); //这个返回的结果又是什么？
        }
        //不存在则抛出异常
        return Result.error("没有查询到默认地址");
    }
}
