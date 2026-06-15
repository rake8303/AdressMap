let googleMapsPromise = null

export function loadGoogleMapsScript() {
  if (window.google?.maps) {
    return Promise.resolve(window.google.maps)
  }

  if (googleMapsPromise) {
    return googleMapsPromise
  }

  const apiKey = import.meta.env.VITE_APP_GOOGLE_MAPS_API_KEY

  googleMapsPromise = new Promise((resolve, reject) => {
    const script = document.createElement('script')
    script.src = `https://maps.googleapis.com/maps/api/js?key=${apiKey}&libraries=marker`
    script.async = true
    script.onload = () => resolve(window.google.maps)
    script.onerror = () => {
      googleMapsPromise = null
      reject(new Error('Google Maps API 加载失败'))
    }
    document.head.appendChild(script)
  })

  return googleMapsPromise
}
