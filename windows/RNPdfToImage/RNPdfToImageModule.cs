using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Pdf.To.Image.RNPdfToImage
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNPdfToImageModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNPdfToImageModule"/>.
        /// </summary>
        internal RNPdfToImageModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNPdfToImage";
            }
        }
    }
}
