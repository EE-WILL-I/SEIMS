select "l1", sum(val_r2_10) as "value" from oo1_vrr23 join oo1_r23_1 r1 on r1.id = oo1_vrr23.id_r1 where r1.id = 1 group by id_r1
 union all
  select "l2", sum(val_r2_12) as "value" from oo1_vrr23 join oo1_r23_1 r1 on r1.id = oo1_vrr23.id_r1 where r1.id = 1 group by id_r1;