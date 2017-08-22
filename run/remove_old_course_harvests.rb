#!/usr/bin/env ruby

require 'date'
require 'fileutils'
require_relative 'terms'

def date
  Date::today
end

def previous_term_hash(date)
  terms.each_with_index do |term, ix|
    return terms[ix-1][:end_date] if term[:end_date] >= date and terms[ix-1][:end_date] < date
  end
end

puts "\nMoving old harvested course files, harvested before #{
      Date::MONTHNAMES[previous_term_hash(date).month]
    } #{previous_term_hash(date).year}"

Dir.glob('/s/SUL/Harvester/out/course_harvest.out.*') do |filename|
  file = File.new(filename)
	mtime = file.mtime
  mdate = mtime.strftime('%Y-%m-%d')

	dir = '/s/SUL/Harvester/out/OldCourses'
	dirname = File.dirname(dir)
  FileUtils.mkdir(dir) unless Dir.exist?(dir)

	if previous_term_hash(date) > Date.parse(mdate)
    puts "Moving #{filename} into #{dir}"
    FileUtils.mv(filename, "#{dir}/")
  end
end
