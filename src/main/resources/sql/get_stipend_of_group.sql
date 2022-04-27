select s2.name, s2.value
from People p
         join People_stipend ps on ps.id_people = p.id
         join Sfofi s on s.id  = p.id_sfofi
         join Stipend s2 on s2.id = ps.id_stipend
where s.speciality_id = @a0 and s.`year`= @a1 and s.`Group` = @a2
group by s2.id