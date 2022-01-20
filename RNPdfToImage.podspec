
Pod::Spec.new do |s|
  s.name         = "RNPdfToImage"
  s.version      = "1.0.1"
  s.summary      = "RNPdfToImage"
  s.description  = <<-DESC
                  RNPdfToImage 1.0.1
                   DESC
  s.homepage     = "nohomepage"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/cmpayc/react-native-pdf-to-image", :tag => "master" }
  s.source_files  = "ios/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  