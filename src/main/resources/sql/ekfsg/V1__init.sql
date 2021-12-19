create table file_content(id UUID, content BLOB);

create table file(
  id UUID,
  content_id UUID,
  filename VARCHAR,
  mime_type VARCHAR,
  created_at TIMESTAMP,
  user_id varchar
);

create table invoice(
  id UUID,
  amount_cents INTEGER,
  user_comment VARCHAR,
  internal_comment VARCHAR,
  file_id UUID,
  ssn VARCHAR,
  iban VARCHAR,
  created_at TIMESTAMP,
  user_id VARCHAR,
  user_mail VARCHAR,
  state VARCHAR
);

create table mail(
    id UUID,
    "from" VARCHAR,
    "to" VARCHAR,
    subject VARCHAR,
    body VARCHAR,
    sent_at TIMESTAMP
);