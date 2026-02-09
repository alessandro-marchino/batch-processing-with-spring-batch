DROP TABLE IF EXISTS subjects_learning;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS student_output;
DROP TABLE IF EXISTS department;

CREATE TABLE department (
  id bigint PRIMARY KEY,
  dept_name varchar(45) DEFAULT NULL
);

CREATE TABLE student (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  first_name varchar(45),
  last_name varchar(45),
  email varchar(45),
  dept_id bigint DEFAULT NULL,
  is_active tinyint DEFAULT NULL,
  KEY dept_id_fk_idx (dept_id),
  CONSTRAINT dept_id_fk FOREIGN KEY (dept_id) REFERENCES department (id)
);

CREATE TABLE student_output (
  id bigint PRIMARY KEY AUTO_INCREMENT,
  original_id bigint,
  first_name varchar(45),
  last_name varchar(45),
  email varchar(45)
);

CREATE TABLE subjects_learning (
  id bigint PRIMARY KEY,
  sub_name varchar(45),
  student_id bigint,
  marks_obtained bigint,
  KEY student_id_fk_idx (student_id),
  CONSTRAINT student_id_fk FOREIGN KEY (student_id) REFERENCES student (id)
);
