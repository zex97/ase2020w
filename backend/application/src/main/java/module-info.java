module com.studyboard.application {
	requires com.studyboard.persistence;
	requires com.studyboard.rest;
	requires com.studyboard.security;

	requires java.instrument;
	requires java.sql;
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.beans;
	requires spring.context;

	exports com.studyboard;
	exports com.studyboard.application;

	opens com.studyboard to spring.core;
	opens com.studyboard.application to spring.core;
}
