//
//  FacebookController.h
//  Unity-iPhone
//
//  Created by Roman Spiryagin on 4/13/10.
//  Copyright 2010 __MyCompanyName__. All rights reserved.
//
#import "FBConnect.h"

@interface FacebookController : UIViewController<FBSessionDelegate, FBRequestDelegate> {
	Facebook *facebook;
	NSArray *permissions;
}

@end
