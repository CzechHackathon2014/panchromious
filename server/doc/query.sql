select mean_euclidean_distances.name as name,
       round(mean_values.red) as mean_red,
       round(mean_values.green) as mean_green,
       round(mean_values.blue) as mean_blue,
       mean_euclidean_distances.my_euclidean_distance as my_distance,
       case when mean_euclidean_distances.mean_euclidean_distance = 0 then 0 else mean_euclidean_distances.my_euclidean_distance / mean_euclidean_distances.mean_euclidean_distance end as doubt
from (select votes.name as name,
             avg(sqrt(power(votes.red - mean_values.red, 2) + power(votes.green - mean_values.green, 2) + power(votes.blue - mean_values.blue, 2))) as mean_euclidean_distance,
             max(sqrt(power(<red> - mean_values.red, 2) + power(<green> - mean_values.green, 2) + power(<blue> - mean_values.blue, 2))) as my_euclidean_distance       
      from (select name as name, 
              avg(red) as red, 
              avg(green) as green, 
              avg(blue) as blue 
             from votes group by name) mean_values inner join votes votes on mean_values.name = votes.name
       group by votes.name) mean_euclidean_distances inner join (select name as name, 
                           avg(red) as red, 
                           avg(green) as green, 
                           avg(blue) as blue 
                         from votes group by name) mean_values on mean_euclidean_distances.name = mean_values.name
order by my_distance, doubt
 limit 8;
