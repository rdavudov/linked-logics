package io.linkedlogics.model.process.helper;

import java.util.ArrayList;
import java.util.List;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.ErrorLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;

public class LogicFinder {
	public static List<BaseLogicDefinition> findLogics(ProcessDefinition process, Class<? extends BaseLogicDefinition> logicDefinitionClass) {
		List<BaseLogicDefinition> foundList = new ArrayList<>();
		traverse(process.getLogics(), foundList, logicDefinitionClass);
		return foundList;
	}
	
	private static void traverse(List<BaseLogicDefinition> logicList, List<BaseLogicDefinition> foundList, Class<? extends BaseLogicDefinition> logicDefinitionClass) {
		logicList.forEach(l -> {
			if (logicDefinitionClass.isAssignableFrom(l.getClass())) {
				foundList.add(l);
			}
			
			if (l instanceof BranchLogicDefinition) {
				BranchLogicDefinition branch = (BranchLogicDefinition) l;
				traverse(List.of(branch.getLeftLogic()), foundList, logicDefinitionClass);
				if (branch.getRightLogic() != null) {
					traverse(List.of(branch.getRightLogic()), foundList, logicDefinitionClass);
				}
			} else if (l instanceof GroupLogicDefinition) {
				GroupLogicDefinition group = (GroupLogicDefinition) l;
				traverse(group.getLogics(), foundList, logicDefinitionClass);
			} else if (l instanceof SingleLogicDefinition) {
				SingleLogicDefinition single = (SingleLogicDefinition) l;
				if (single.getCompensationLogic() != null) {
					traverse(List.of(single.getCompensationLogic()), foundList, logicDefinitionClass);
				}
			} 
//			else if (l instanceof ErrorLogicDefinition) {
//				ErrorLogicDefinition error = (ErrorLogicDefinition) l;
//				if (error.getErrorLogic() != null) {
//					traverse(List.of(error.getErrorLogic()), foundList, logicDefinitionClass);
//				}
//			}
			
			if (l.getErrors() != null && l.getErrors().size() > 0) {
				l.getErrors().stream().filter(e -> e.getErrorLogic() != null).forEach(e -> traverse(List.of(e.getErrorLogic()), foundList, logicDefinitionClass));
			}
		});
	}
}
