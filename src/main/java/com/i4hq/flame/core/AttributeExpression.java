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
    	IN,
    	FROM,
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
    
    private String attributeName;
    private Operator operator;
    private Geo2DPoint[] coordinates;
    private Literal[] literals;
    private AttributeExpression[] subExprs;
	private EntityType entityType;
	private int limit = -1;
    
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
    
    public static AttributeExpression withinExpr (String attributeName, Geo2DPoint pos1, Geo2DPoint pos2, Geo2DPoint pos3, Geo2DPoint ... morePositions){
    	AttributeExpression expr = new AttributeExpression();
    	expr.operator = Operator.WITHIN;
  
    	expr.coordinates = new Geo2DPoint[3 + morePositions.length];
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
    
    public static AttributeExpression fromType (EntityType entityType){
    	if (entityType == null){
    		throw new RuntimeException("entityType cannot be null.");
    	}
    	AttributeExpression expr = new AttributeExpression();
    	expr.operator = Operator.FROM;
  
    	expr.entityType = entityType;
    	
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
	 * Get the coordinates. The first and last point are always equal.
	 * If the coordinates are the points  coordinates are 
	 * @return Returns the coordinates
	 */
	public Geo2DPoint[] getCoordinates() {
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

	/**
	 * @return the entityType
	 */
	public EntityType getEntityType() {
		return entityType;
	}

	public int getLimit() {
		return limit;
	}

	/**
	 * Limit the number of responses to the given value. A non-positive value means do not limit responses.
	 * @param limit
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}
    
    
}
