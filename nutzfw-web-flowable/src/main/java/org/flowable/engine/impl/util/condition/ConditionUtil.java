package org.flowable.engine.impl.util.condition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nutzfw.core.common.util.RegexUtil;
import com.nutzfw.core.plugin.flowable.FlowServiceSupport;
import com.nutzfw.core.plugin.flowable.converter.element.CustomSequenceFlow;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.DynamicBpmnConstants;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.Condition;
import org.flowable.engine.impl.context.BpmnOverrideContext;
import org.flowable.engine.impl.el.UelExpressionCondition;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.nutz.dao.Sqls;
import org.nutz.dao.entity.Record;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * @author threefish
 */
public class ConditionUtil {

    private static final String TYPE = "sql";

    public static boolean hasTrueCondition(SequenceFlow sequenceFlowParame, DelegateExecution execution) {
        final CustomSequenceFlow sequenceFlow = CustomSequenceFlow.of(sequenceFlowParame);
        String conditionExpression;
        if (CommandContextUtil.getProcessEngineConfiguration().isEnableProcessDefinitionInfoCache()) {
            ObjectNode elementProperties = BpmnOverrideContext.getBpmnOverrideElementProperties(sequenceFlow.getId(), execution.getProcessDefinitionId());
            conditionExpression = getActiveValue(sequenceFlow.getConditionExpression(), DynamicBpmnConstants.SEQUENCE_FLOW_CONDITION, elementProperties);
        } else {
            conditionExpression = sequenceFlow.getConditionExpression();
        }

        if (StringUtils.isNotEmpty(conditionExpression)) {
            if (TYPE.equals(sequenceFlow.getType())) {
                return getConditionValueBySql(conditionExpression, execution);
            } else {
                Expression expression = CommandContextUtil.getProcessEngineConfiguration().getExpressionManager().createExpression(conditionExpression);
                Condition condition = new UelExpressionCondition(expression);
                return condition.evaluate(sequenceFlow.getId(), execution);
            }
        } else {
            return true;
        }
    }

    private static boolean getConditionValueBySql(String conditionExpression, DelegateExecution execution) {
        final Map<String, VariableInstance> variableInstances = execution.getVariableInstances();
        Map<String, Object> params = new HashMap(variableInstances.size());
        variableInstances.forEach((key, variableInstance) -> params.put(key, variableInstance.getValue()));
        Sql sql = Sqls.create(conditionExpression);
        sql.setCallback(Sqls.callback.record());
        sql.setParams(params);
        FlowServiceSupport.dao().execute(sql);
        Record record = sql.getObject(Record.class);
        String result = record.getString("result");
        if (Strings.isNotBlank(result) && RegexUtil.isInteger(result)) {
            int resultInteger = record.getInt("result");
            return resultInteger > 0;
        }
        return BooleanUtils.toBoolean(result);
    }

    protected static String getActiveValue(String originalValue, String propertyName, ObjectNode elementProperties) {
        String activeValue = originalValue;
        if (elementProperties != null) {
            JsonNode overrideValueNode = elementProperties.get(propertyName);
            if (overrideValueNode != null) {
                if (overrideValueNode.isNull()) {
                    activeValue = null;
                } else {
                    activeValue = overrideValueNode.asText();
                }
            }
        }
        return activeValue;
    }

}
