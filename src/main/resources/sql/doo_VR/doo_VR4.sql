SELECT doo.id_buildokud, doo2.name as 'Численность воспитанников - всего',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=1 and bo.id_build = @a0)) as '0 лет',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=2 and bo.id_build = @a0)) as '1 год',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=3 and bo.id_build = @a0)) as '2 года',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=4 and bo.id_build = @a0)) as '3 года',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=5 and bo.id_build = @a0)) as '4 года',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=6 and bo.id_build = @a0)) as '5 лет',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=7 and bo.id_build = @a0)) as '6 лет',
       (SELECT doo1.value from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and doo1.id_r4_2=8 and bo.id_build = @a0)) as '7 лет и старше',
       (SELECT sum(doo1.value) from doo_vr4 doo1 join buildokud bo on bo.id = doo1.id_buildokud where (doo1.id_r4_1=doo.id_r4_1 and bo.id_build = @a0)) as 'Всего' FROM doo_vr4 doo
         join buildokud bo on bo.id = doo.id_buildokud
         join build b on b.id=bo.id_build
         join doo_r4_1 doo2 on doo2.id =doo.id_r4_1 where b.id = @a0 group by doo.id_r4_1