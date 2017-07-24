# require 'bundler'
require 'fileutils'

namespace :book do
  # 文件名
  file = "byte-buddy-tutorial"
  # Path
  project_dir = Dir.pwd
  book_dir = "#{project_dir}/book"
  target_dir = "#{project_dir}/target"
  images_dir = "#{book_dir}/images"
  images_target_dir = "#{target_dir}/images/images"

  # 删除存着的文件
  def delete_file(file)
    if File.exist? file
      puts "Deleting: #{file}"
      if File.file? file
        File.delete file
      else
        FileUtils.rm_r file
      end
    end
  end

  desc 'Clear build result'
  task :clear do
    ["target", "#{file}.html", "#{file}.pdf", "#{file}.epub", "#{file}.mobi"].each do |file|
      delete_file file
    end

    puts "Clear ok"
  end

  desc 'prepare build'
  task :prebuild => :clear do
    print "\nStart to copy images...\n"
    # Dir.mkdir 'images' unless Dir.exists? 'images'
    FileUtils.mkdir_p images_target_dir
    Dir.glob("#{images_dir}/*").each do |image|
      FileUtils.copy(image, "#{images_target_dir}/" + File.basename(image))
    end
    print "Finish copying images.\n\n"
  end

  desc 'build basic book formats'
  task :build => :prebuild do
    puts "Converting to HTML..."
    `bundle exec asciidoctor -r asciidoctor-diagram #{file}.adoc`
    puts " -- HTML output at #{file}.html"
    `open #{file}.html`

    # puts "Converting to PDF..."
    # `wkhtmltopdf #{file}.html #{file}.pdf`
    # puts " -- PDF output at #{file}.pdf"

    # puts "Converting to PDF by asciidoctor-pdf..."
    # `bundle exec asciidoctor-pdf -r asciidoctor-diagram #{file}.adoc`
    # puts " -- PDF output at #{file}.pdf"

    # puts "Converting to HTML..."
    # `asciidoctor -r ./config.rb #{file}.adoc`
    # puts " -- HTML output at #{file}.html"
    #
    # puts "Converting to EPub..."
    # `bundle exec asciidoctor-epub3 -r ./config.rb #{file}.adoc`
    # puts " -- Epub output at #{file}.epub"
    #
    # puts "Converting to Mobi (kf8)..."
    # `bundle exec asciidoctor-epub3 -r ./config.rb -a ebook-format=kf8 #{file}.adoc`
    # puts " -- Mobi output at #{file}.mobi"

    #puts "Converting to PDF... (this one takes a while)"
    #`bundle exec asciidoctor-pdf -r ./config.rb -a pdf-style=KaiGenGothicCN #{file}.adoc`
    #puts " -- PDF  output at #{file}.pdf"
  end
end

task :default => "book:build"
