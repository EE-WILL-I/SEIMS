SELECT @a0 from people where id not in (
    SELECT Id from people p
        inner join people_stipend ps on p.id = ps.id_people
)