package net.iharding.modules.etl.controller;

import org.guess.core.web.BaseController;
import net.iharding.modules.etl.model.EtlTaskParam;
import net.iharding.modules.etl.service.EtlTaskParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* 
* @ClassName: EtlTaskParam
* @Description: EtlTaskParamController
* @author Joe.zhang
* @date  2016-1-30 17:38:18
*
*/
@Controller
@RequestMapping("/etl/EtlTaskParam")
public class EtlTaskParamController extends BaseController<EtlTaskParam>{

	{
		editView = "/etl/EtlTaskParam/edit";
		listView = "/etl/EtlTaskParam/list";
		showView = "/etl/EtlTaskParam/show";
	}
	
	@Autowired
	private EtlTaskParamService etlTaskParamService;
}
