package net.iharding.modules.job.service.impl;

import java.util.Date;

import net.iharding.modules.job.dao.JobFlowDao;
import net.iharding.modules.job.dao.JobWorkerDao;
import net.iharding.modules.job.model.JobFlowCron;
import net.iharding.modules.job.model.JobFlowWrapper;
import net.iharding.modules.job.model.JobWorker;
import net.iharding.modules.job.service.JobWorkerService;

import org.guess.core.service.BaseServiceImpl;
import org.guess.sys.model.User;
import org.guess.sys.util.UserUtil;
import org.quartz.CronExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.uncode.schedule.ConsoleManager;

/**
* 
* @ClassName: JobWorker
* @Description: JobWorkerserviceImpl
* @author Joe.zhang
* @date 2014-8-5 下午02:04:46
*
*/
@Service
public class JobWorkerServiceImpl extends BaseServiceImpl<JobWorker, Long> implements JobWorkerService {
	@Autowired
	private JobFlowDao jobFlowDao;
	
	@Autowired
	private JobWorkerDao jobWorkerDao;
	
	@Transactional
	@Override
	public JobWorker schedleJob(JobFlowWrapper object,JobFlowCron cron, String cronString) {
		JobWorker jworker=jobWorkerDao.getByKey(object.getJobclass().getClassName(),object.getJobclass().getMethodName(),cron.getJobParameter());
		User user=UserUtil.getCurrentUser();
		boolean isNew=false;
		if (jworker==null){
			jworker=new JobWorker();
			jworker.setCreateDate(new Date());
			jworker.setCreater(user);
			isNew=true;
		}
		jworker.setJobclass(object.getJobclass());
		jworker.setCron(cronString);
		jworker.setName(cron.getName());
		jworker.setLogicName(cron.getLogicName());
		jworker.setJobClassName(object.getJobclass().getClassName());
		jworker.setMethodName(object.getJobclass().getMethodName());
		jworker.setCheckLabel(1);
		jworker.setJobParameter(cron.getJobParameter());
		jworker.setUpdateDate(new Date());
		jworker.setUpdater(user);
		jworker.setJobflow(object);
		jworker.setRemark(cron.getRemark());
		jobWorkerDao.saveOrUpdate(jworker);
		if (isNew)jworker=jobWorkerDao.getByKey(jworker.getJobClassName(),jworker.getMethodName(),jworker.getJobParameter());
		ConsoleManager.addScheduleTask(jworker.covertoTaskDefint());
		return jworker;
	}
	
	@Override
	public void save(JobWorker object, String start) throws Exception {
		JobWorker jworker=jobWorkerDao.get(object.getId());
		jworker.setName(object.getName());
		jworker.setCron(object.getCron());
		jworker.setCheckLabel(1);
		jworker.setJobParameter(object.getJobParameter());
		jworker.setRemark(object.getRemark());
		jworker.setUpdateDate(new Date());
		jworker.setUpdater(UserUtil.getCurrentUser());
		CronExpression exp = new CronExpression(object.getCron());
		jworker.setNextExeDate(exp.getNextValidTimeAfter(new Date()));
		if ("1".equalsIgnoreCase(start)){//提交任务到调度系统
			ConsoleManager.addScheduleTask(jworker.covertoTaskDefint());
		}
		jobWorkerDao.update(jworker);
	}

}
