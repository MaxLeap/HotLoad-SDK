Pod::Spec.new do |s|

  s.name                = 'HotLoad'
  s.version             = '1.8.0-beta'
  s.summary             = 'React Native plugin for the HotLoad service'
  s.author              = 'Microsoft Corporation'
  s.license             = 'MIT'
  s.homepage            = ''
  s.source              = { :git => '', :tag => "v#{s.version}" }
  s.platform            = :ios, '7.0'
  s.source_files        = 'ios/HotLoad/*.{h,m}', 'ios/HotLoad/SSZipArchive/*.{h,m}', 'ios/HotLoad/SSZipArchive/aes/*.{h,c}', 'ios/HotLoad/SSZipArchive/minizip/*.{h,c}'
  s.public_header_files = 'ios/HotLoad/HotLoad.h'
  s.preserve_paths      = '*.js'
  s.library             = 'z'
  s.dependency 'React'

end
