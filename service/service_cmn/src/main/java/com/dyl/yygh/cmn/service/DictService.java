package com.dyl.yygh.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dyl.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {
    //根据数据id查询子数据列表
    List<Dict> findChlidData(Long id);
    //导出数据字典接口
    void exportData(HttpServletResponse response);

    void importData(MultipartFile file);
}
