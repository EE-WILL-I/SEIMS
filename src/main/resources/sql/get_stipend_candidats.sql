select @a0 from people where can_receive_stipend = 1
  and people.id not in (select p.id from people p inner join people_stipend ps on p.id = ps.id_people)
  and people.id in (select p1.id from people p1 inner join people_privileges pp on p1.id = pp.student_id)