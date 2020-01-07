#!/usr/bin/env ruby

def terms
  [
    {:term => "Winter 2012", :quarter => "Winter", :end_date => Date.new(2012, 3, 23)},
    {:term => "Spring 2012", :quarter => "Spring", :end_date => Date.new(2012, 6, 13)},
    {:term => "Summer 2012", :quarter => "Summer", :end_date => Date.new(2012, 8, 18)},
    {:term => "Fall 2012",   :quarter => "Fall",   :end_date => Date.new(2012, 12, 14)},

    {:term => "Winter 2013", :quarter => "Winter", :end_date => Date.new(2013, 3, 22)},
    {:term => "Spring 2013", :quarter => "Spring", :end_date => Date.new(2013, 6, 12)},
    {:term => "Summer 2013", :quarter => "Summer", :end_date => Date.new(2013, 8, 17)},
    {:term => "Fall 2013",   :quarter => "Fall",   :end_date => Date.new(2013, 12, 13)},

    {:term => "Winter 2014", :quarter => "Winter", :end_date => Date.new(2014, 3, 21)},
    {:term => "Spring 2014", :quarter => "Spring", :end_date => Date.new(2014, 6, 11)},
    {:term => "Summer 2014", :quarter => "Summer", :end_date => Date.new(2014, 8, 16)},
    {:term => "Fall 2014",   :quarter => "Fall",   :end_date => Date.new(2014, 12, 12)},

    {:term => "Winter 2015", :quarter => "Winter", :end_date => Date.new(2015, 3, 20)},
    {:term => "Spring 2015", :quarter => "Spring", :end_date => Date.new(2015, 6, 10)},
    {:term => "Summer 2015", :quarter => "Summer", :end_date => Date.new(2015, 8, 15)},
    {:term => "Fall 2015",   :quarter => "Fall",   :end_date => Date.new(2015, 12, 11)},

    {:term => "Winter 2016", :quarter => "Winter", :end_date => Date.new(2016, 3, 18)},
    {:term => "Spring 2016", :quarter => "Spring", :end_date => Date.new(2016, 6, 8)},
    {:term => "Summer 2016", :quarter => "Summer", :end_date => Date.new(2016, 8, 13)},
    {:term => "Fall 2016",   :quarter => "Fall",   :end_date => Date.new(2016, 12, 16)},

    {:term => "Winter 2017", :quarter => "Winter", :end_date => Date.new(2017, 3, 24)},
    {:term => "Spring 2017", :quarter => "Spring", :end_date => Date.new(2017, 6, 14)},
    {:term => "Summer 2017", :quarter => "Summer", :end_date => Date.new(2017, 8, 19)},
    {:term => "Fall 2017",   :quarter => "Fall",   :end_date => Date.new(2017, 12, 15)},

    {:term => "Winter 2018", :quarter => "Winter", :end_date => Date.new(2018, 3, 23)},
    {:term => "Spring 2018", :quarter => "Spring", :end_date => Date.new(2018, 6, 13)},
    {:term => "Summer 2018", :quarter => "Summer", :end_date => Date.new(2018, 8, 18)},
    {:term => "Fall 2018",   :quarter => "Fall",   :end_date => Date.new(2018, 12, 14)},

    {:term => "Winter 2019", :quarter => "Winter", :end_date => Date.new(2019, 3, 22)},
    {:term => "Spring 2019", :quarter => "Spring", :end_date => Date.new(2019, 6, 12)},
    {:term => "Summer 2019", :quarter => "Summer", :end_date => Date.new(2019, 8, 17)},

    {:term => "Fall 2019",   :quarter => "Fall",   :end_date => Date.new(2019, 12, 13)},
    {:term => "Winter 2020", :quarter => "Winter", :end_date => Date.new(2020, 3, 20)},
    {:term => "Spring 2020", :quarter => "Spring", :end_date => Date.new(2020, 6, 10)},
    {:term => "Summer 2020", :quarter => "Summer", :end_date => Date.new(2020, 8, 15)},

    {:term => "Fall 2020",   :quarter => "Fall",   :end_date => Date.new(2020, 12, 11)},
    {:term => "Winter 2021", :quarter => "Winter", :end_date => Date.new(2021, 3, 19)},
    {:term => "Spring 2021", :quarter => "Spring", :end_date => Date.new(2021, 6, 9)},
    {:term => "Summer 2021", :quarter => "Summer", :end_date => Date.new(2021, 8, 14)},

    {:term => "Fall 2021",   :quarter => "Fall",   :end_date => Date.new(2021, 12, 10)},
    {:term => "Winter 2022", :quarter => "Winter", :end_date => Date.new(2022, 3, 18)},
    {:term => "Spring 2022", :quarter => "Spring", :end_date => Date.new(2022, 6, 8)},
    {:term => "Summer 2022", :quarter => "Summer", :end_date => Date.new(2022, 8, 13)},

    {:term => "Fall 2022",   :quarter => "Fall",   :end_date => Date.new(2022, 12, 16)},
    {:term => "Winter 2023", :quarter => "Winter", :end_date => Date.new(2023, 3, 24)},
    {:term => "Spring 2023", :quarter => "Spring", :end_date => Date.new(2023, 6, 14)},
    {:term => "Summer 2023", :quarter => "Summer", :end_date => Date.new(2023, 8, 19)},

    {:term => "Fall 2023",   :quarter => "Fall",   :end_date => Date.new(2023, 12, 15)},
    {:term => "Winter 2024", :quarter => "Winter", :end_date => Date.new(2024, 3, 22)},
    {:term => "Spring 2024", :quarter => "Spring", :end_date => Date.new(2024, 6, 12)},
    {:term => "Summer 2024", :quarter => "Summer", :end_date => Date.new(2024, 8, 17)},

    {:term => "Fall 2024",   :quarter => "Fall",   :end_date => Date.new(2024, 12, 13)},
    {:term => "Winter 2025", :quarter => "Winter", :end_date => Date.new(2025, 3, 21)},
    {:term => "Spring 2025", :quarter => "Spring", :end_date => Date.new(2025, 6, 11)},
    {:term => "Summer 2025", :quarter => "Summer", :end_date => Date.new(2025, 8, 16)},

    {:term => "Fall 2025",   :quarter => "Fall",   :end_date => Date.new(2025, 12, 12)},
    {:term => "Winter 2026", :quarter => "Winter", :end_date => Date.new(2026, 3, 22)},
    {:term => "Spring 2026", :quarter => "Spring", :end_date => Date.new(2026, 6, 10)},
    {:term => "Summer 2026", :quarter => "Summer", :end_date => Date.new(2026, 8, 15)},

    {:term => "Fall 2026",   :quarter => "Fall",   :end_date => Date.new(2026, 12, 11)},
    {:term => "Winter 2027", :quarter => "Winter", :end_date => Date.new(2027, 3, 19)},
    {:term => "Spring 2027", :quarter => "Spring", :end_date => Date.new(2027, 6, 9)},
    {:term => "Summer 2027", :quarter => "Summer", :end_date => Date.new(2027, 8, 14)},

    {:term => "Fall 2027",   :quarter => "Fall",   :end_date => Date.new(2027, 12, 10)},
    {:term => "Winter 2028", :quarter => "Winter", :end_date => Date.new(2028, 3, 17)},
    {:term => "Spring 2028", :quarter => "Spring", :end_date => Date.new(2028, 6, 7)},
    {:term => "Summer 2028", :quarter => "Summer", :end_date => Date.new(2028, 8, 12)},

    {:term => "Fall 2028",   :quarter => "Fall",   :end_date => Date.new(2028, 12, 15)},
    {:term => "Winter 2029", :quarter => "Winter", :end_date => Date.new(2029, 3, 23)},
    {:term => "Spring 2029", :quarter => "Spring", :end_date => Date.new(2029, 6, 13)},
    {:term => "Summer 2029", :quarter => "Summer", :end_date => Date.new(2029, 8, 18)},

    {:term => "Fall 2029",   :quarter => "Fall",   :end_date => Date.new(2029, 12, 14)},
    {:term => "Winter 2030", :quarter => "Winter", :end_date => Date.new(2030, 3, 22)},
    {:term => "Spring 2030", :quarter => "Spring", :end_date => Date.new(2030, 6, 12)},
    {:term => "Summer 2030", :quarter => "Summer", :end_date => Date.new(2030, 8, 17)}
  ]
end
