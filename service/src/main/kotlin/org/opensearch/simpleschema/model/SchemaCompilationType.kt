package org.opensearch.simpleschema.model

import org.opensearch.common.io.stream.StreamInput
import org.opensearch.common.io.stream.StreamOutput
import org.opensearch.common.io.stream.Writeable
import org.opensearch.common.xcontent.XContentParser
import org.opensearch.common.xcontent.XContentFactory
import org.opensearch.common.xcontent.ToXContent
import org.opensearch.common.xcontent.XContentBuilder
import org.opensearch.common.xcontent.XContentParserUtils
import org.opensearch.simpleschema.SimpleSchemaPlugin.Companion.LOG_PREFIX
import org.opensearch.simpleschema.util.fieldIfNotNull
import org.opensearch.simpleschema.util.logger
import org.opensearch.simpleschema.util.stringList

/**
 * This element represents a schema entity which is stored as a document in the DB
 * The entity has a type, name, catalog, description and the actual GraphQL SDL as content
 *
 * Content is stored as object IDs in the entities list, which references GraphQL components such as:
 * - Types
 * - Mutations
 * - Queries
 * - Subscriptions
 * Compilation and transformation generally will involve retrieving this data.
 *
 * The catalog represent the general belonging of this specific entity - this entity may belong to multiple catalogs
 */
internal data class SchemaCompilationType(
    val name: String,
    val entities: List<String>,
    val description: String?,//nullable
    val catalog: List<String>?,//nullable
) : BaseObjectData {

    internal companion object {
        private val log by logger(SchemaCompilationType::class.java)
        private const val NAME_TAG = "name"
        private const val DESCRIPTION_TAG = "description"
        private const val CATALOG_TAG = "catalog"
        private const val ENTITIES_TAG = "entities"

        /**
         * reader to create instance of class from writable.
         */
        val reader = Writeable.Reader { SchemaCompilationType(it) }

        /**
         * Parser to parse xContent
         */
        val xParser = XParser { parse(it) }

        /**
         * Parse the data from parser and create SchemaCompilation
         * @param parser data referenced at parser
         * @return created SchemaCompilation object
         */
        fun parse(parser: XContentParser): SchemaCompilationType {
            var entities: List<String>? = null
            var description: String? = null //nullable
            var catalog: List<String>? = null //nullable
            var name: String? = null //nullable
            XContentParserUtils.ensureExpectedToken(
                XContentParser.Token.START_OBJECT,
                parser.currentToken(),
                parser
            )
            while (XContentParser.Token.END_OBJECT != parser.nextToken()) {
                val fieldName = parser.currentName()
                parser.nextToken()
                when (fieldName) {
                    NAME_TAG -> name = parser.text()
                    DESCRIPTION_TAG -> description = parser.text()
                    CATALOG_TAG -> catalog = parser.stringList()
                    ENTITIES_TAG -> entities = parser.stringList()
                    else -> {
                        parser.skipChildren()
                        log.info("$LOG_PREFIX:SchemaType Skipping Unknown field $fieldName")
                    }
                }
            }
            name ?: throw IllegalArgumentException("Required field '$NAME_TAG' is absent")
            entities ?: throw java.lang.IllegalArgumentException("Required field '$ENTITIES_TAG' is absent")
            return SchemaCompilationType(name, entities, description, catalog)
        }
    }


    /**
     * create XContentBuilder from this object using [XContentFactory.jsonBuilder()]
     * @param params XContent parameters
     * @return created XContentBuilder object
     */
    fun toXContent(params: ToXContent.Params = ToXContent.EMPTY_PARAMS): XContentBuilder? {
        return toXContent(XContentFactory.jsonBuilder(), params)
    }

    /**
     * Constructor used in transport action communication.
     * @param input StreamInput stream to deserialize data from.
     */
    constructor(input: StreamInput) : this(
        name = input.readString(),
        description = input.readOptionalString(), //nullable
        entities = input.readStringList(),
        catalog = input.readOptionalStringList(), //nullable
    )

    /**
     * {@inheritDoc}
     */
    override fun writeTo(output: StreamOutput) {
        output.writeString(name) //nullable
        output.writeOptionalString(description) //nullable
        output.writeStringCollection(entities)
        output.writeOptionalStringCollection(catalog) //nullable
    }

    /**
     * {@inheritDoc}
     */
    override fun toXContent(builder: XContentBuilder?, params: ToXContent.Params?): XContentBuilder {
        builder!!
        builder.startObject()
            .fieldIfNotNull(NAME_TAG, name) //nullable
            .fieldIfNotNull(DESCRIPTION_TAG, description) //nullable
            .fieldIfNotNull(CATALOG_TAG, catalog) //nullable
            .fieldIfNotNull(ENTITIES_TAG, entities)
        return builder.endObject()
    }
}
