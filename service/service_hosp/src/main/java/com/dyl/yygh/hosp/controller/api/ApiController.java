package com.dyl.yygh.hosp.controller.api;

import com.dyl.hosp.common.helper.HttpRequestHelper;
import com.dyl.hosp.common.utils.MD5;
import com.dyl.yygh.common.exception.YyghException;
import com.dyl.yygh.common.result.Result;
import com.dyl.yygh.common.result.ResultCodeEnum;
import com.dyl.yygh.hosp.service.DepartmentService;
import com.dyl.yygh.hosp.service.HospitalService;
import com.dyl.yygh.hosp.service.HospitalSetService;
import com.dyl.yygh.hosp.service.ScheduleService;
import com.dyl.yygh.model.hosp.Department;
import com.dyl.yygh.model.hosp.Schedule;
import com.dyl.yygh.vo.hosp.DepartmentQueryVo;
import com.dyl.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalSetService hospitalSetService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private ScheduleService scheduleService;
    @ApiOperation(value = "获取医院信息")
    @PostMapping("hospital/show")
    public Result hospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        String signParam = (String) paramMap.get("sign");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        String signKey = hospitalSetService.getSignKey(hoscode);
        String signKeyMd5 = MD5.encrypt(signKey);
        if (!signParam.equals(signKeyMd5)){
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        /*//签名校验
        if(!HttpRequestHelper.isSignEquals(paramMap, hospitalSetService.getSignKey(hoscode))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }*/

        return Result.ok(hospitalService.getByHoscode((String)paramMap.get("hoscode")));
    }

    @ApiOperation(value = "上传医院")
    @PostMapping("saveHospital")
    public Result saveHospital(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());

        //必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        String sign = (String) paramMap.get("sign");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        String signQu = hospitalSetService.getSignKey(hoscode);
        //签名校验
        if(!sign.equals(MD5.encrypt(signQu))) {
            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
        }
        //传输过程中“+”转换为了“ ”，因此我们要转换回来
        String logoDataString = (String)paramMap.get("logoData");
        if(!StringUtils.isEmpty(logoDataString)) {
            String logoData = logoDataString.replaceAll(" ", "+");
            paramMap.put("logoData", logoData);
        }
        hospitalService.save(paramMap);
        return Result.ok();
    }

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略
        String hoscode = (String)paramMap.get("hoscode");

        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //签名校验
        String sign = (String) paramMap.get("sign");
        String signQu = hospitalSetService.getSignKey(hoscode);
        String signMd5 = MD5.encrypt(signQu);
//        if(!sign.equals(signMd5)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        departmentService.save(paramMap);
        return Result.ok();
    }
    @ApiOperation(value = "获取分页列表")
    @PostMapping("department/list")
    public Result department(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
            //必须参数校验
        String hoscode = (String)paramMap.get("hoscode");
        //非必填
        String depcode = (String)paramMap.get("depcode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String)paramMap.get("limit"));

        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        String sign = (String) paramMap.get("sign");
        String signQu = hospitalSetService.getSignKey(hoscode);
        String signMd5 = MD5.encrypt(signQu);
//        if(!sign.equals(signMd5)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        DepartmentQueryVo departmentQueryVo = new DepartmentQueryVo();
        departmentQueryVo.setHoscode(hoscode);
        departmentQueryVo.setDepcode(depcode);
        Page<Department> pageModel = departmentService.selectPage(page, limit, departmentQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result removeDepartment(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略
        String hoscode = (String)paramMap.get("hoscode");
        //必填
        String depcode = (String)paramMap.get("depcode");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        String sign = (String) paramMap.get("sign");
        String signQu = hospitalSetService.getSignKey(hoscode);
        String signMd5 = MD5.encrypt(signQu);
        //        if(!sign.equals(signMd5)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }
        departmentService.remove(hoscode, depcode);
        return Result.ok();
    }
    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
//必须参数校验 略
        String hoscode = (String) paramMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        String sign = (String) paramMap.get("sign");
        String signQu = hospitalSetService.getSignKey(hoscode);
        String signMd5 = MD5.encrypt(signQu);
        //        if(!sign.equals(signMd5)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }
        scheduleService.save(paramMap);
        return Result.ok();
    }
    @ApiOperation(value = "获取排班分页列表")
    @PostMapping("schedule/list")
    public Result schedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
//必须参数校验 略
        String hoscode = (String)paramMap.get("hoscode");
//非必填
        String depcode = (String)paramMap.get("depcode");
        int page = StringUtils.isEmpty(paramMap.get("page")) ? 1 : Integer.parseInt((String)paramMap.get("page"));
        int limit = StringUtils.isEmpty(paramMap.get("limit")) ? 10 : Integer.parseInt((String)paramMap.get("limit"));

        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //签名校验
        String sign = (String) paramMap.get("sign");
        String signQu = hospitalSetService.getSignKey(hoscode);
        String signMd5 = MD5.encrypt(signQu);
        //        if(!sign.equals(signMd5)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        ScheduleQueryVo scheduleQueryVo = new ScheduleQueryVo();
        scheduleQueryVo.setHoscode(hoscode);
        scheduleQueryVo.setDepcode(depcode);
        Page<Schedule> pageModel = scheduleService.selectPage(page , limit, scheduleQueryVo);
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "删除科室")
    @PostMapping("schedule/remove")
    public Result removeSchedule(HttpServletRequest request) {
        Map<String, Object> paramMap = HttpRequestHelper.switchMap(request.getParameterMap());
//必须参数校验 略
        String hoscode = (String)paramMap.get("hoscode");
//必填
        String hosScheduleId = (String)paramMap.get("hosScheduleId");
        if(StringUtils.isEmpty(hoscode)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //签名校验
        String sign = (String) paramMap.get("sign");
        String signQu = hospitalSetService.getSignKey(hoscode);
        String signMd5 = MD5.encrypt(signQu);
        //        if(!sign.equals(signMd5)) {
//            throw new YyghException(ResultCodeEnum.SIGN_ERROR);
//        }

        scheduleService.remove(hoscode, hosScheduleId);
        return Result.ok();
    }


}