<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/content/common/common.jsp"%>
<%@ include file="/WEB-INF/content/common/plugins/page.jsp"%>
<script type="text/javascript" src="${ctx}/assets/js/map.js"></script>
<script type="text/javascript">
	$(function(){
		var dbtypeMap = new Map();  
		<mytags:dictSelect field="dbtypeMap" id="dbtypeMap" type="map" hasLabel="false" codeType="10" />
		Page.initData(
				{
					url:"${ctx}/meta/DataSource/page",
					pageNo : 1,
					pageSize : 10,
					checkboxType : 1,
					colName : "dsName",
					tableId : "#datasources"
				},
				null,
				[
					{cName:"dsName",cValue:"ds名"},
		 	{cName:"schemaName",cValue:"schema"},
		 	
			 	{cName:"dbType",cValue:"类别",format:function(i,value,item){
			 		return dbtypeMap.get(item.dbType);
			 	}},
			 	{cName:"remark",cValue:"备注"}
				 ]
			);
	});
</script>
<div id="datasourceList" class="modal hide fade" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal"
			aria-hidden="true">&times;</button>
		<h3>选择数据源</h3>
	</div>
	<div class="modal-body">
		<table class="table table-striped table-bordered table-hover" id="datasources">
		</table>
	</div>
	<div class="modal-footer">
		<button type="button" class="btn green" onclick="javascript:selDataSource();">确定</button>
		<button type="button red" class="btn" data-dismiss="modal"
			aria-hidden="true">关闭</button>
	</div>
</div>
