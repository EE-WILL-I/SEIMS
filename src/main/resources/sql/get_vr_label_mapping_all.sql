select vr_name, r1_name, r2_name, update_type,
       (select count(id) from vr_update_type_mapping) as 'vr_count'
from vr_update_type_mapping where id between
        (select min(id) from vr_update_type_mapping)+@a0 and
        (select min(id) from vr_update_type_mapping)+@a1 order by id;