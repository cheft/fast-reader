-- Create table
create table FILE_CONTENT
(
  ID            VARCHAR2(32) not null,
  NAME          VARCHAR2(300),
  LENGTH        NUMBER(16),
  SUFFIX        VARCHAR2(30),
  CONTENT       BLOB,
  FLASHCONTENT  BLOB,
  MD5           VARCHAR2(50),
  SRCFILEPATH   VARCHAR2(3000),
  FLASHFILEPATH VARCHAR2(3000),
  ATTACHMENTID  VARCHAR2(32),
  SYSTEMNAME    VARCHAR2(300),
  CONVERTCOUNT  NUMBER(8),
  TYPE          VARCHAR2(300),
  DEPTID        VARCHAR2(32),
  DEPTNAME      VARCHAR2(300),
  ORGID         VARCHAR2(32),
  ORGNAME       VARCHAR2(300),
  CREATORID     VARCHAR2(32),
  CREATORNAME   VARCHAR2(30),
  CREATEDATE    DATE
)
tablespace AUDIT_FLEX
  pctfree 10
  initrans 1
  maxtrans 255
  storage
  (
    initial 64
    minextents 1
    maxextents unlimited
  );
-- Create/Recreate primary, unique and foreign key constraints 
alter table FILE_CONTENT
  add primary key (ID)
  using index 
  tablespace AUDIT_FLEX
  pctfree 10
  initrans 2
  maxtrans 255
  storage
  (
    initial 64K
    minextents 1
    maxextents unlimited
  );