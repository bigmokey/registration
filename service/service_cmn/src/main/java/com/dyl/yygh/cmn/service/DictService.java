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
    /**
     * 根据上级编码与值获取数据字典名称
     * @param parentDictCode
     * @param value
     * @return
     */
    String getNameByParentDictCodeAndValue(String parentDictCode, String value);

    List<Dict> findByDictCode(String dictCode);
}
