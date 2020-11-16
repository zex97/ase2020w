module com.studyboard.persistence {
	requires transitive com.studyboard.domain;

	requires com.fasterxml.classmate;
	requires net.bytebuddy;
	requires spring.data.jpa;
	requires spring.tx;
	requires spring.data.commons;
	requires jdk.unsupported;

//	exports com.studyboard.persist;

//	opens com.studyboard.persist to org.hibernate.orm.core, spring.core;
}
