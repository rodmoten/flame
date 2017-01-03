/**
 * 
 */
package com.i4hq.flame.core;

/**
 * TODO
 * @author rmoten
 *
 */
public class AttributeExpression {
	/*
	 * expr ::= logicOp (expr1, expr2, ..., expr_n)
	 *         | relOp(ATTR_NAME,  LITERAL)
	 *         | WITHIN(ATTR_NAME, COORD_1 COORD_2 COORD_3 ... COORD_n)
	 *         | IN(ATTR_NAME, LITERAL_1 ... LITERAL_n)
	 *  logicOp ::= AND | OR 
	 *  relOP ::= EQ | LT | LTE | GT | GTE | NEQ
	 */
	
    public enum Operator {
    	AND,
    	OR,
    	EQ,
    	LT,
    	LTE,
    	GT,
    	GTE,
    	NEQ,
    	WITHIN,
    	IN
    	;	
    }
    
    public class Literal{
    	private final AttributeType type;
    	private final String value;
		/**
		 * @param type
		 * @param value
		 */
		public Literal(AttributeType type, String value) {
			super();
			this.type = type;
			this.value = value;
		}
		/**
		 * @return the type
		 */
		public AttributeType getType() {
			return type;
		}
		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}
    }
    
    public String attributeName;
    public Operator operator;
    public GeospatialPosition[] coordinates;
    public Literal[] literals;
    public AttributeExpression[] subExprs;
    
    /**
     * Create an AND attribute expression.
     * @param arg1 
     * @param arg2
     * @param args
     * @return
     */
    public static AttributeExpression andExpr (AttributeExpression arg1, AttributeExpression arg2, AttributeExpression ... args){
    	return logicExprs(Operator.AND, arg1, arg2, args);
    }
    
    /**
     * Create an OR attribute expression.
     * @param arg1 
     * @param arg2
     * @param args
     * @return
     */
    public static AttributeExpression orExpr (AttributeExpression arg1, AttributeExpression arg2, AttributeExpression ... args){
    	return logicExprs(Operator.OR, arg1, arg2, args);
    }
    
    public static AttributeExpression eqExpr (String attributeName, Literal literal){
    	return relExpr(Operator.EQ, attributeName, literal);
    }
    
    public static AttributeExpression neqExpr (String attributeName, Literal literal){
    	return relExpr(Operator.NEQ, attributeName, literal);
    }
    
    public static AttributeExpression gtExpr (String attributeName, Literal literal){
    	return relExpr(Operator.GT, attributeName, literal);
    }
    
    public static AttributeExpression gteExpr (String attributeName, Literal literal){
    	return relExpr(Operator.GTE, attributeName, literal);
    }
    
    public static AttributeExpression ltExpr (String attributeName, Literal literal){
    	return relExpr(Operator.LT, attributeName, literal);
    }
    
    public static AttributeExpression lteExpr (String attributeName, Literal literal){
    	return relExpr(Operator.LTE, attributeName, literal);
    }
    
    public static AttributeExpression withinExpr (String attributeName, GeospatialPosition pos1, GeospatialPosition pos2, GeospatialPosition pos3, GeospatialPosition ... morePositions){
    	AttributeExpression expr = new AttributeExpression();
    	expr.operator = Operator.WITHIN;
  
    	expr.coordinates = new GeospatialPosition[3 + morePositions.length];
    	expr.coordinates[0] = pos1;
    	expr.coordinates[1] = pos2;
    	expr.coordinates[2] = pos3;
    	System.arraycopy(morePositions, 0, expr.coordinates, 3, morePositions.length);
    	
    	return expr;
    }
    
    public static AttributeExpression inExpr (String attributeName, Literal ... literals){
    	AttributeExpression expr = new AttributeExpression();
    	expr.operator = Operator.IN;
  
    	expr.literals = new Literal[literals.length];
    	System.arraycopy(literals, 0, expr.literals, 0, literals.length);
    	
    	return expr;
    }
    

    private static AttributeExpression logicExprs (Operator operator, AttributeExpression arg1, AttributeExpression arg2, AttributeExpression ... args){
    	AttributeExpression expr = new AttributeExpression();
    	expr.operator = operator;
  
    	expr.subExprs = new AttributeExpression[2 + args.length];
    	expr.subExprs[0] = arg1;
    	expr.subExprs[1] = arg2;
    	System.arraycopy(args, 0, expr.subExprs, 2, args.length);
    	
    	return expr;
    }
    

    private static AttributeExpression relExpr (Operator operator, String attributeName, Literal literal){
    	AttributeExpression expr = new AttributeExpression();
    	expr.operator = operator;
    	expr.attributeName = attributeName;
    	expr.literals = new Literal[]{literal};    	
    	return expr;
    }

	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * @return the operator
	 */
	public Operator getOperator() {
		return operator;
	}

	/**
	 * @return the coordinates
	 */
	public GeospatialPosition[] getCoordinates() {
		return coordinates;
	}

	/**
	 * @return the literals
	 */
	public Literal[] getLiterals() {
		return literals;
	}

	/**
	 * @return the subExprs
	 */
	public AttributeExpression[] getSubExprs() {
		return subExprs;
	}
    
    
}
