#!/usr/bin/env ruby

require 'date'
require 'fileutils'
require_relative 'terms'

def date
  Date::today
end

def current_term_hash(date)
  terms.each_with_index do |term, ix|
    return term[:end_date] if term[:end_date] >= date and terms[ix-1][:end_date] < date
  end
end

def next_quarter(date)
  terms.each_with_index do |term, ix|
    return terms[ix+1][:quarter] if term[:end_date] == date
  end
end

def second_next_quarter(date)
  terms.each_with_index do |term, ix|
    return terms[ix+2][:quarter] if term[:end_date] == date
  end
end

puts "\nUpdating terms.conf with current terms"
thisq = terms.select{ |hash| hash[:end_date] == current_term_hash(date) }[0][:quarter]
nextq = next_quarter(current_term_hash(date)).downcase
next2q = second_next_quarter(current_term_hash(date)).downcase
File.open('/s/SUL/Harvester/conf/terms.conf', 'w') do |terms_file|
  puts "TERMS=#{thisq},#{nextq},#{next2q}"
  terms_file.syswrite("TERMS=#{thisq},#{nextq},#{next2q}\n")
end
