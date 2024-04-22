package com.bentoco.catalog.dynamodb.utils;

import com.bentoco.catalog.core.model.AbstractModel;
import com.bentoco.catalog.utils.StringUtils;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.ConditionCheck;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.UUID;

import static com.bentoco.catalog.constants.AwsConstants.PREFIX_OWNER;
import static com.bentoco.catalog.constants.AwsConstants.PREFIX_CATEGORY_TITLE;

public class DynamoDbUtils {

    public static <T> ConditionCheck<T> getConditionCheck(final Key key, final Expression expression) {
        return ConditionCheck.builder()
                .key(key)
                .conditionExpression(expression)
                .build();
    }

    public static Expression buildMustBeUniqueTitleAndOwnerIdExpression(final String title, final String ownerId) {
        return Expression.builder()
                .expression("#t <> :tv AND #o <> :ov")
                .putExpressionName("#t", "Title")
                .putExpressionName("#o", "OwnerID")
                .putExpressionValue(":tv", AttributeValue.builder().s(title).build())
                .putExpressionValue(":ov", AttributeValue.builder().s(ownerId).build())
                .build();
    }

    public static Expression buildMustBeUniqueTitleAndOwnerIdExpression() {
        return Expression.builder()
                .expression("attribute_not_exists(#pk)")
                .expressionNames(Map.of("#pk", "pk"))
                .build();
    }

    public static Expression buildMustExistsExpression() {
        return Expression.builder()
                .expression("attribute_exists(#pk)")
                .expressionNames(Map.of("#pk", "pk"))
                .build();
    }

    public static Key getKey(final String id) {
        return Key.builder()
                .partitionValue(id)
                .build();
    }

    public static Key getKey(final UUID id, final String prefix) {
        String partitionKey = StringUtils.prefixedId(id.toString(), prefix);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }

    public static Key getKey(final String id, final String prefix) {
        String partitionKey = StringUtils.prefixedId(id, prefix);
        return Key.builder()
                .partitionValue(partitionKey)
                .build();
    }

    /**
     * This is an example of result: OWNER#1a2b3c#TITLE#Foo
     */
    public static String getUniquenessConstraint(String key, String title) {
        return STR. "\{ key }\{ PREFIX_CATEGORY_TITLE }\{ title }" ;
    }

    public static <T extends AbstractModel> TransactPutItemEnhancedRequest<T> transactPutItemRequest(T model) {
        return TransactPutItemEnhancedRequest.builder((Class<T>) model.getClass())
                .item(model)
                .conditionExpression(buildMustBeUniqueTitleAndOwnerIdExpression())
                .build();
    }
}
