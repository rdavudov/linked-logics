package io.linkedlogics.spring.service;

import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParseException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import io.linkedlogics.service.EvaluatorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SpringEvaluatorService implements EvaluatorService {
	private ExpressionParser parser;
	
	public SpringEvaluatorService() {
		parser = new SpelExpressionParser();
	}
	
	@Override
	public Object evaluate(String expression, Map<String, Object> params) {
		StandardEvaluationContext context = new StandardEvaluationContext(params);
		context.addPropertyAccessor(new MapAccessor());
	    try {
			return parser.parseExpression(expression).getValue(context);
		} catch (EvaluationException|ParseException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	    return null;
	}
}
