package com.mars.admin.support.extend;

import com.mars.admin.support.AbstractRepositoryTestSupport;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RepositoryTestCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (context.getTestClass().isPresent() &&
            AbstractRepositoryTestSupport.class.isAssignableFrom(context.getTestClass().get())) {
            return ConditionEvaluationResult.disabled("Repository 테스트는 최종 통합 테스트에서 제외됩니다.");
        }
        return ConditionEvaluationResult.enabled("Repository 테스트가 아닙니다.");
    }
}
