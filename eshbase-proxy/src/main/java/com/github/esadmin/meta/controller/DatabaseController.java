package com.github.esadmin.meta.controller;

import org.guess.core.web.BaseController;
import com.github.esadmin.meta.model.Database;
import com.github.esadmin.meta.service.DatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
* 
* @ClassName: Database
* @Description: DatabaseController
* @author Joe.zhang
* @date  2016-5-18 14:08:46
*
*/
@Controller
@RequestMapping("/meta/Database")
public class DatabaseController extends BaseController<Database>{

	{
		editView = "/meta/Database/edit";
		listView = "/meta/Database/list";
		showView = "/meta/Database/show";
	}
	
	@Autowired
	private DatabaseService databaseService;
}