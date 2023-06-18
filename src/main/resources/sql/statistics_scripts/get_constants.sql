select "sum of orgs" as "fields", count(*) as "values" from build
 union all
 select "all students", sum(val_r2_1) from oo1_vrr3
 union all
 select "sum of classes", sum(val_r2_1) from oo1_vrr5
 union all
 select "sum of classes for disabled", sum(val_r2_1) from oo1_vrr8
 union all
 select "sum of dist classes", sum(val_r2_1) from oo1_vrr17
 union all
 select "graduated students", sum(val_r2_10)+sum(val_r2_12) as "value" from oo1_vrr23 join oo1_r23_1 r1 on r1.id = oo1_vrr23.id_r1 where r1.id = 1 group by id_r1;