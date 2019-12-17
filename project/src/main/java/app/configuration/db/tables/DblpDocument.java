package app.configuration.db.tables;
// Generated Sep 27, 2019, 11:41:57 PM by Hibernate Tools 5.2.12.Final

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * DblpDocument generated by hbm2java
 */
@Entity
@Table(name = "dblp_document")
public class DblpDocument implements java.io.Serializable {

	private DblpDocumentId id;

	public DblpDocument() {
	}

	public DblpDocument(DblpDocumentId id) {
		this.id = id;
	}

	@EmbeddedId

	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "id", nullable = false)),
			@AttributeOverride(name = "keyDblp", column = @Column(name = "key_dblp", length = 100)),
			@AttributeOverride(name = "authors", column = @Column(name = "authors")),
			@AttributeOverride(name = "docType", column = @Column(name = "doc_type", length = 100)),
			@AttributeOverride(name = "editor", column = @Column(name = "editor", length = 100)),
			@AttributeOverride(name = "booktitle", column = @Column(name = "booktitle", length = 250)),
			@AttributeOverride(name = "pages", column = @Column(name = "pages", length = 50)),
			@AttributeOverride(name = "year", column = @Column(name = "year")),
			@AttributeOverride(name = "title", column = @Column(name = "title")),
			@AttributeOverride(name = "address", column = @Column(name = "address")),
			@AttributeOverride(name = "journal", column = @Column(name = "journal")),
			@AttributeOverride(name = "volume", column = @Column(name = "volume", length = 100)),
			@AttributeOverride(name = "number", column = @Column(name = "number", length = 50)),
			@AttributeOverride(name = "month", column = @Column(name = "month", length = 50)),
			@AttributeOverride(name = "url", column = @Column(name = "url")),
			@AttributeOverride(name = "ee", column = @Column(name = "ee")),
			@AttributeOverride(name = "cdrom", column = @Column(name = "cdrom")),
			@AttributeOverride(name = "cite", column = @Column(name = "cite")),
			@AttributeOverride(name = "publisher", column = @Column(name = "publisher")),
			@AttributeOverride(name = "note", column = @Column(name = "note")),
			@AttributeOverride(name = "crossref", column = @Column(name = "crossref")),
			@AttributeOverride(name = "isbn", column = @Column(name = "isbn")),
			@AttributeOverride(name = "series", column = @Column(name = "series")),
			@AttributeOverride(name = "school", column = @Column(name = "school")),
			@AttributeOverride(name = "chapter", column = @Column(name = "chapter")),
			@AttributeOverride(name = "publnr", column = @Column(name = "publnr")),
			@AttributeOverride(name = "unknowFields", column = @Column(name = "unknow_fields")),
			@AttributeOverride(name = "unknowAtts", column = @Column(name = "unknow_atts")),
			@AttributeOverride(name = "mdate", column = @Column(name = "mdate", length = 100)),
			@AttributeOverride(name = "updated", column = @Column(name = "updated")) })
	public DblpDocumentId getId() {
		return this.id;
	}

	public void setId(DblpDocumentId id) {
		this.id = id;
	}

}