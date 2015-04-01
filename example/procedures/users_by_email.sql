create or replace function users_by_email(user_email varchar(64))
  returns setof users as $$
begin
  return query
  select *
  from users u
  where u.email = user_email;
end
$$ language plpgsql security definer;

