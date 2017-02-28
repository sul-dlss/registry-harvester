#!/usr/bin/env ruby

require 'date'
require 'fileutils'

def date
  Date::today
end

def current_term_hash(date)
  terms.each_with_index do |term, ix|
    return terms[ix-1][:end_date] if term[:end_date] >= date and terms[ix-1][:end_date] < date
  end
end

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
    {:term => "Summer 2020", :quarter => "Summer", :end_date => Date.new(2020, 8, 15)}
  ]
end

puts "Moving old harvested course files, harvested before #{
      Date::MONTHNAMES[current_term_hash(date).month]
    } #{current_term_hash(date).year}"

Dir.glob('/s/SUL/Harvester/out/course_harvest.out.*') do |filename|
  file = File.new(filename)
	mtime = file.mtime

	dir = '/s/SUL/Harvester/out/OldCourses'
	dirname = File.dirname(dir)
  FileUtils.mkdir(dir) unless Dir.exist?(dir)

	if current_term_hash(date).month > Date.parse("#{mtime.strftime('%Y-%m-%d')}").month
    puts "Moving #{filename} into #{dir}"
    FileUtils.mv(filename, "#{dir}/")
  end
end
