
#import "RNPdfToImage.h"
#import "UIImage+PDF.h"

@implementation RNPdfToImage

RCT_EXPORT_MODULE()

RCT_EXPORT_METHOD(convert:(NSString *)filePath
                  findEventsWithResolver:(RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject)
{

    CGFloat newWidth = 200;
    NSURL *url = [NSURL fileURLWithPath:filePath];
    UIImage *img = [ UIImage imageWithPDFURL:url atWidth:(CGFloat)newWidth atPage:1 ];
    CGFloat scale = img.scale;
    CGFloat width = img.size.width * scale;
    CGFloat height = img.size.height * scale;
    
    NSString *newPath = [ self createTempFile:img ];
    NSURL *fileURL = [NSURL fileURLWithPath:newPath];
    NSNumber *fileSizeValue = nil;
    NSError *fileSizeError = nil;
    [fileURL getResourceValue:&fileSizeValue
                       forKey:NSURLFileSizeKey
                        error:&fileSizeError];
    if (!fileSizeValue) {
        NSLog(@"error getting size for url %@ error was %@", fileURL, fileSizeError);
        reject(@"file error", @"error getting size for url %@ error was %@", nil);
        return;
    }
    
    NSMutableDictionary *fileData = [[NSMutableDictionary alloc] init];
    [fileData setObject:newPath forKey:@"file"];
    [fileData setObject:[NSNumber numberWithFloat:width] forKey:@"width"];
    [fileData setObject:[NSNumber numberWithFloat:height] forKey:@"height"];
    [fileData setObject:fileSizeValue forKey:@"size"];

    resolve(fileData);
}

- (NSString *)createTempFile:(UIImage *)image
{
    NSString *fileID = [[NSUUID UUID] UUIDString];
    NSString *pathComponent = [NSString stringWithFormat:@"Documents/rctpdf-snapshot-%@.%@", fileID, @"png"];
    NSString *filePath = [NSHomeDirectory() stringByAppendingPathComponent: pathComponent];
    
    NSData *data = UIImagePNGRepresentation(image);
    [data writeToFile:filePath atomically:YES];
    
    return filePath;
}

- (NSString *)createBase64:(UIImage *)image
{
    NSData *data = UIImagePNGRepresentation(image);
    return [NSString stringWithFormat:@"%@%@",  @"data:image/png;base64,", [data base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithCarriageReturn]];
}
@end
  
